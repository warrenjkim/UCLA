# Make x86-64 random byte generators.

# Copyright 2015, 2020, 2021 Paul Eggert

# This program is free software: you can redistribute it and/or
# modify it under the terms of the GNU General Public License as
# published by the Free Software Foundation, either version 3 of the
# License, or (at your option) any later version.

# This program is distributed in the hope that it will be useful, but
# WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
# General Public License for more details.

# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.  */

# Optimization level.  Change this -O2 to -Og or -O0 or whatever.
OPTIMIZE = -O2

# The C compiler and its options.
CC = gcc
CFLAGS = $(OPTIMIZE) -g3 -Wall -Wextra -fanalyzer \
  -march=native -mtune=native -mrdrnd

# The archiver command, its options and filename extension.
TAR = tar
TARFLAGS = --gzip --transform 's,^,randall/,'
TAREXT = tgz

# checkscript
define checkscript =
{ \
if [ $$(./randall 5000 | wc -c) -eq 5000 ] && \
   [ $$(./randall 2000 -i /bin/diff | wc -c) -eq 2000 ] && \
   [ $$(./randall 5000 -i rdrand | wc -c) -eq 5000 ] && \
   [ !$$(./randall 5 -i /some/invalid/file >/dev/null 2>&1) ] && \
   [ !$$(./randall 5 -f /invalid >/dev/null 2>&1) ] && \
   [ !$$(./randall 5 -i >/dev/null 2>&1) ] && \
   [ $$(./randall 10000 -i mrand48_r | wc -c) -eq 10000 ] && \
   [ $$(./randall 10000 -o stdio | wc -c) -eq 10000 ] && \
   [ $$(./randall 10000 -o 23 | wc -c) -eq 10000 ] && \
   [ $$(./randall 10000 -i /bin/diff -o 323 | wc -c) -eq 10000 ] && \
   [ !$$(./randall 10000 -i /bin/diff 2039 -o bad args >/dev/null 2>&1) ]; \
then \
    echo 'check successful'; \
    exit 0; \
else \
  echo 'ERROR: argument count not equal to byte count'; \
  exit 1; \
fi; \
}
endef

default: randall

randall: randall.c rand64-hw.c rand64-sw.c options.c output.c
	$(CC) $(CFLAGS) randall.c rand64-hw.c rand64-sw.c options.c output.c -o $@

assignment: randall-assignment.$(TAREXT)
assignment-files = COPYING Makefile randall.c
randall-assignment.$(TAREXT): $(assignment-files)
	$(TAR) $(TARFLAGS) -cf $@ $(assignment-files)

submission-tarball: randall-submission.$(TAREXT)
submission-files = $(assignment-files) \
  notes.txt # More files should be listed here, as needed.
randall-submission.$(TAREXT): $(submission-files)
	$(TAR) $(TARFLAGS) -cf $@ $(submission-files)

repository-tarball:
	$(TAR) -czf randall-git.tgz .git

.PHONY: check default clean assignment submission-tarball repository-tarball


check: randall	
	@$(checkscript)

clean:
	rm -f *.o *.$(TAREXT) randall
