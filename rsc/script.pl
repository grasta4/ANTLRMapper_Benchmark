#!/usr/bin/perl

use strict;
use warnings;

sub makeSolutions {
	my $problem = $_[0];
	my $dlv = '';
	my @instances = `ls problems/asp/$problem/instances`;

	if(defined $_[1]) {
		$dlv = 'DLV,';	
	}

	for my $instance (@instances) {
		my $encoding = 'encoding.asp';
		my $instanceCopy = $instance;
    	my $outputName = substr($instanceCopy, 0, 4).'.out';		
		my $limit = int(rand(30)) + 1;

		chomp($instance);

		print "Making output (limit: <$limit>) of Clingo, $dlv DLV2 with instance <$instance> of <$problem> -> ";
		`./clingo -n $limit problems/asp/$problem/instances/$instance problems/asp/$problem/$encoding > problems/asp/$problem/clingoOutputs/$outputName`;
		print 'Clingo DONE, ';

		if($dlv ne '') {
			`./dlv -n=$limit problems/asp/$problem/instances/$instance problems/asp/$problem/$encoding > problems/asp/$problem/dlvOutputs/$outputName`;
			print 'DLV DONE, ';
		}
		
		`./dlv2 -n $limit problems/asp/$problem/instances/$instance problems/asp/$problem/$encoding > problems/asp/$problem/dlv2Outputs/$outputName`;
		print "DLV2 DONE\n";
	}
}

die("Invalid number of args, must be at least 1 (the problem)\n") if(@ARGV < 1);
makeSolutions($ARGV[0], $ARGV[1]);
