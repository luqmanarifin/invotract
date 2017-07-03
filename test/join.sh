#!/bin/bash

cnt=0
lines=""
for f in 1B.arff; do
  cnt=$((cnt+1))
  echo "File -> $f"
  echo "$cnt"th file
  while read line; do
    if [[ $line == @* ]]; then
      if [[ $cnt > 1 ]]; then
        lines+=$line
        lines+="\n"
      fi
    else
      lines+=$line
      lines+="\n"
    fi
  done <$f
done
printf "$lines"
printf "$lines">result.txt
echo $cnt files!
