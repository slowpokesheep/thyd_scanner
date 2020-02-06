#!/usr/bin/env python3

import sys


sys.stdout.write('-' * 80)
sys.stdout.write('\n')

for line in sys.stdin:
  if (line[:2] == '-1'):
    # Bright red and reset
    sys.stdout.write(f'\033[1;31m{line}\033[0;0m')
  else:
    sys.stdout.write(line)

sys.stdout.write('-' * 80)
sys.stdout.write('\n')
