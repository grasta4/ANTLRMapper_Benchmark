#!/usr/bin/perl

die("Provide path\n") if (!defined $ARGV[0]);

my @solverName;

if($ARGV[0] =~ /asp/) {
	@solverName = ('clingo', 'dlv', 'dlv2');
} elsif($ARGV[0] =~ /pddl/){
	@solverName = ('spd');
}

foreach(@solverName) {
	chomp $_;
	
	my $path = "$ARGV[0]/$_".'Outputs';
	my @fileName = `ls $path/parserSolutions/`;
	
	foreach(@fileName) {
		chomp $_;
		print "Comparing $path/parserSolutions/$_ $path/regexSolutions/$_ -> ";
		
		my $result = `diff --brief $path/parserSolutions/$_ $path/regexSolutions/$_`;

		if($result) {
			print $result;
		} else {
			print "Files are equals.\n"		
		}
	}
}
