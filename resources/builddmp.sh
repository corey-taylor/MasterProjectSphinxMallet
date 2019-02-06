#!/bin/bash
echo "Building dmp....."
./sphinx_lm_convert -i resources/input.lm -o resources/model.dmp
echo "Done."