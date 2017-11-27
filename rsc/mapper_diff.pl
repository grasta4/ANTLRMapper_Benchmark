#!/usr/bin/perl

die("Provide path\n") if (!defined $ARGV[0]);

my $path = "$ARGV[0]';
my @fileName = `ls $path/mapperSolutions/`;
	
foreach(@fileName) {
	chomp $_;
	print "Comparing $path/mapperSolutions/$_ $path/oldMapperSolutions/$_ -> ";
		
	my $result = `diff --brief $path/parserSolutions/$_ $path/oldMapperSolutions/$_`;

	if($result) {
		print $result;
	} else {
		print "Files are equals.\n"		
	}
}