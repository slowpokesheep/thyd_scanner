#!/usr/bin/env python3

import sys

none='\033[0;0m'
red='\033[1;31m'
green='\033[1;32m'
yellow='\033[1;33m'
blue='\033[1;34m'

sys.stdout.write('-' * 80)
sys.stdout.write('\n')

for line in sys.stdin:
  
  w = line.split()

  if (w[0] == "Exception"):
  
    for i in range(len(w)):
      if (w[i-1] == "Expected" or w[i-1] == "found"):
        sys.stdout.write(f'{red}{w[i]} {none}')
      else:
        sys.stdout.write(f'{w[i]} ')
    sys.stdout.write('\n')
  else:
    if (w[0] == "Accepted!"):
      sys.stdout.write(f'{green}{line}{none}')
    else:
      sys.stdout.write(line)

  """
  if (line[:2] == '-1'):
    # Bright red and reset
    sys.stdout.write(f'\033[1;31m{line}\033[0;0m')
  else:
    sys.stdout.write(line)
  """

sys.stdout.write('-' * 80)
sys.stdout.write('\n')
