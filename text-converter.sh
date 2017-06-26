#!/bin/bash

cnt=0

for f in *.png; do
  echo "File -> $f"
  cnt=$((cnt+1))
  minus=$((${#f}-4))
  echo $minus
  g=${f:0:$minus}
  echo $g
  tesseract "$f" "$g"
done

echo $cnt files!
