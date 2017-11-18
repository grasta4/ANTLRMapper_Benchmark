connected(a,b).
connected(a,f).
connected(b,c).
connected(b,d).
connected(c,e).
connected(d,e).
connected(f,e).

node(X) :- connected(X,Y).
node(Y) :- connected(X,Y).

reaches(X,X) :- node(X), not offline(X).
reaches(X,Z) :- reaches(X,Y), connected(Y,Z), not offline(Z).
