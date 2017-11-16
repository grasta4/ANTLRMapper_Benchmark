#!/usr/bin/perl

use strict;
use warnings;

sub makeSolutions {
	my $problem = $_[0]; 	
	my @instances = `ls problems/asp/$problem/instances`;

	for my $instance (@instances) {
		my $encoding = 'encoding.asp';
		my $instanceCopy = $instance;
    	my $outputName = substr($instanceCopy, 0, 4).'.out';		
		my $limit = int(rand(30)) + 1;

		chomp($instance);

		print "Making output (limit: <$limit>) of Clingo, DLV2 with instance <$instance> of <$problem> -> ";
		`./clingo -n $limit problems/asp/$problem/instances/$instance problems/asp/$problem/$encoding > problems/asp/$problem/clingoOutputs/$outputName`;
		print 'Clingo DONE, ';		
		`./dlv2 -n $limit problems/asp/$problem/instances/$instance problems/asp/$problem/$encoding > problems/asp/$problem/dlv2Outputs/$outputName`;
		print "DLV2 DONE\n";
	}
}

die("Invalid number of args, must be 1") if(@ARGV != 1);
makeSolutions($ARGV[0]);
