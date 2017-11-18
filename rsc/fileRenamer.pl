#!/usr/bin/perl

#Put in directory where you need to change files' name.
#Applies standard format: (0*)n.asp.
#0+ prefix only if the string length of n is < 4.

use strict;
use warnings;

my $counter = '1';

foreach(`ls`) {
	chomp $_;

	if('fileRenamer.pl' eq $_) {
		next;	
	}

	my $zeroes = "0" x (4 - length($counter));			
	my $newName = $zeroes.$counter++.'.asp';

	`mv $_ $newName`;
}
