"""
Author: Jacob Peterson
Program: LexiGuess
Date: 10/15/2017
Client-Server hangman game using sockets.
The Client and Server will both be implemented in lexiguess.py
"""


import socket
import sys
import argparse
from struct import *
import os


"""
- Checks command line and splits into client or server code
"""
def main():
    args = arg_parse()
    word = str(args.word)

    if args.mode == 'server':
        if word == 'None':
            print("In server mode you need to specify a word with --word")
            sys.exit(-1)
        server(args.port, args.ip, args.word)
    else:
        client(args.port, args.ip)


"""
- Code for the server side
- Port = The port we want to access
- ip = ip address
- word = word for the client to guess
- Responsibilities:
   - Track number of guesses and letters remaining
   - Check if the word contains the letter
   - Tell the client when they win or lose
   - Tell the client where the correct guessed letters are located
"""
def server(port, ip, word):
    guesses_left = 3
    remaining_letters = len(word)
    word_list = list(word)
    s_descript = socket.socket(socket.AF_INET, socket.SOCK_STREAM, 0)
    try:
        s_descript.bind((ip, port))
    except socket.error as e:
        print(str(e))
        sys.exit(-1)
    s_descript.listen(5)
    while True:
        conn, addr = s_descript.accept()
        newpid = os.fork()
        if newpid == 0:
            while guesses_left > 0:
                send(conn, guesses_left)
                send(conn, remaining_letters)
                user_guess = recv(conn)
                letter_positions = check_guess(word_list, user_guess)
                if not letter_positions:
                    guesses_left -= 1
                    send(conn, 0)
                else:
                    send(conn, 1)
                    send(conn, letter_positions)
                    remaining_letters -= len(letter_positions.split(","))
                if (remaining_letters == 0) and (guesses_left > 0):
                    send(conn, -2)
                    conn.close()
                    os._exit(0)
                elif(remaining_letters > 0) and (guesses_left == 0):
                    send(conn, -1)
                    conn.close()
                    os._exit(0)


"""
- Code for the client side
- Responsibilities:
   - Print board state
   - Get input from user
   - Check if user has guessed that letter already
   - Send guess to server
"""
def client(port, ip):
    num_guesses = 3
    board = []
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM, 0)
    sock.connect((ip, port))
    previous_guesses = []
    while num_guesses > 0:
        num_guesses = int(recv(sock))
        if num_guesses == -1:
            board = "".join(board)
            print("Board: " + board + "(0 guesses left)")
            print("You lost")
            sock.close()
            exit(1)
        elif num_guesses == -2:
            board = "".join(board)
            print("Board: " + board.replace(" ", "") + " (0 guesses left)")
            print("You won")
            sock.close()
            exit(1)
        else:
            num_letters = int(recv(sock))
            board = "".join(board)
            if not board:
                for i in range(num_letters):
                    board += "_ "
            print("Board: " + board + "(" + str(num_guesses) + " guesses left)")
            while 1:
                guess = input("Enter guess: ")
                if guess in previous_guesses:
                    print("You already guessed that.")
                else:
                    break
            previous_guesses.append(guess)
            send(sock, guess)
            if int(recv(sock)):
                board = list(board)
                letter_pos = recv(sock).split(",")
                for i in range(len(letter_pos)):
                    board[int(letter_pos[i]) * 2] = guess


"""
- Recv is a function for receiving data through a socket using lexiguess communication protocol
- First receive 4 bytes that gives the length of the incoming data
- Then Receive the exact amount of bytes specified
"""
def recv(connection):
    try:
        data_length = unpack(">i", connection.recv(4, socket.MSG_WAITALL))
        return connection.recv(data_length[0], socket.MSG_WAITALL).decode('utf-8')
    except socket.error as e:
        print(e)
        exit(1)

        
"""
- Send function that sends data through a socket using lexiguess communication protocol
- connection - the socket that the sender is connected to
- message - the message that needs to be sent
- Get the length of the data and send it in 4 bytes then send the data
"""
def send(connection, message):
    try:
        temp_message = str.encode(str(message))
        # Send the size of the message in big-endian
        connection.sendall(pack(">i", len(temp_message)))
        #Send the actual message with the amount of bytes
        connection.sendall(temp_message)
    except socket.error as e:
        print(e)
        exit(1)


"""
- Takes the guesses letter and compares it to the given word
- Returns a comma delimited string of the positions of the guessed letter
- if it returns empty string then the guess is wrong
"""
def check_guess(word, letter):
    pos_list = ""
    i = 0
    for x in range(len(word)):
        if letter == word[x]:
            i += 1
            pos_list += str(x)
            pos_list += ","
    if i > 0:
        pos_list = pos_list[:-1]
    return pos_list


"""
- Parse Arguments
- Handles command line arguments
"""
def arg_parse():
    parse = argparse.ArgumentParser(
        description='LexiGuess, Networked program with server-client options for guessing a word.')
    parse.add_argument('--mode', metavar="m", help="client or server mode", choices=['client', 'server'], required=True)
    parse.add_argument('--port', metavar="p", help="port number", type=int, required=True)
    parse.add_argument('--word', metavar="w", help="word to be guessed")
    parse.add_argument('--ip', metavar="i", help="IP address for client", default="127.0.0.1")
    args = parse.parse_args()
    return args


main()
