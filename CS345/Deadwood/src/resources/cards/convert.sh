#!/bin/bash
#size=$((173X97))
for image  in *.png
do
  convert $image -resize 180X101  $image
done

