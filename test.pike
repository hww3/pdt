int 
main()
{
	int             x;
	array           x1 = Stdio.read_file("out") / "\n";
	mixed           w = gauge {
		do {
			foreach(x1;;
			string          line) {
				string          q;
				multiset        a = (<>);
				multiset        b = (<>);
				                catch(sscanf(line, "%*s\"%s\"", q));
				if              (q)
					              //_Caudium.parse_prestates(q, a, b);
				                parse(q);
				                x++;
			}
			                write("%d\n", x);
		}
		while (x < 10000000);
	};
	werror("w: %O\n", w);
	return 0;
}


multiset 
parse(string q)
{
	multiset        prestate;
	string          a;
	if ((sscanf(q, "/(%s)/%s", a, q) == 2) && strlen(a)) {
		prestate = aggregate_multiset(@(a / "," - ({
			""
		})));
		q = "/" + q;
	}
	return prestate;
}
