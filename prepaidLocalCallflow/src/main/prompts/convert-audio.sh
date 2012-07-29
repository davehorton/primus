#!/bin/bash

shopt -s nullglob
for f in raw/*.wav; do  
	sox ${f} -U raw/pcmu/$(basename $f); 
done
for f in raw/*.wav; do  
	sox ${f} -A raw/pcma/$(basename $f); 
done
