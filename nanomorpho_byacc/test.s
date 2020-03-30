;;; Tests for nanomorpho

;;; Test strings and printing
print_test() {
  var x, y;
  var z;

  y = "Hallo";
  x = "Bye";
  z = "Siggi";

  writeln(x);
  x = "ByeBye";
  writeln(x);

  writeln("Bubbi"++" byggir");
  writeln(x++" "++z);
  printline();
}

;;; Test calculations
calc_test() {
  var x, y, z;
  var result;

  x = 1;
  y = 3;
  z = 5;

  writeln("x = " ++ x ++ " y = " ++ y ++ " z = " ++ z);

  result = 1 + 2 + 3;
  writeln("1 + 2 + 3 = " ++ result);
  
  result = x + y + z;
  writeln("x + y + z = " ++ result);

  result = 4.5 + 3.3 + 6.1;
  writeln("4.5 + 3.3 + 6.1 = " ++ result);

  result = result / 3.1;
  writeln("result / 3.1 = " ++ result);
  printline();
}

;;; Test lists
list_test() {
  var x, y, z, m;
  
  x = 4; y = 3; z = 2;

  write("1:2:3 = "); writeln(1:2:3);
  write("10:20:null = "); writeln(10:20:null);
  writeln("-----");

  writeln("x = " ++ x ++ " y = " ++ y ++ " z = " ++ z);
  writeln("-----");

  write("z:3 = "); writeln(z:3);
  write("x:y:z:3 = "); writeln(x:y:z:3);
  writeln("-----");

  write("head(10:20) = "); writeln(head(10:20));
  write("tail(10:20) = "); writeln(tail(10:20));
  write("tail(z:x):tail(z:y):z = "); writeln(tail(z:x):tail(z:y):z);
  writeln("-----");

  write("\"Bubby\":\"byggir\" = "); writeln("Bubby":"byggir");
  writeln("-----");

  write("Bubbi:null = "); writeln("Bubbi":null);
  printline();
}

;;; Test cond
cond_test(x, y) {

  writeln("x = " ++ x ++ " y = " ++ y);

  if (x) {
    if (y) {
      writeln("x = true y = true");
    }
    else {
      writeln("x = true y = false");
    };
  }
  else {
    if (y) {
      writeln("x = false y = true");
    }
    else {
      writeln("x = false y = false");
    };
  };
  printl();
}

;;; Test more cond
cond2_test(x, y, z) {

  writeln("x = " ++ x ++ " y = " ++ y ++ " z = " ++ z);

  if (x && y && z) {
    writeln("x = true y = true z = true");
  }
  elsif (x || y && z) {
    writeln("x || y && z");
  }
  elsif (x || y && z) {
    writeln("x || y && z");
  }
  elsif (!x && y && !z) {
    writeln("!x && y && !z");
  }
  else {
    writeln("Else");
  };

  printl();
}

;;; Test non-recursive Fibo
fibo(n) {
  var i, f1, f2, tmp;
  f1 = 1;
  f2 = 1;
  i = 0;

  while (i != n) {
    tmp = f1 + f2;
    f1 = f2;
    f2 = tmp;
    i = i + 1;
  };
  printline();
  f1;
}

;;; Test recursive fibo
f(n) {
  if (n < 2) {
    1;
  }
  else {
    f(n-1) + f(n-2);
  };
}

printline() {
 writeln("-------------------------");
}

printl() {
  writeln("----");
}

main() {
  print_test();
  calc_test();
  list_test();

  cond_test(true, true);
  cond_test(true, false);
  cond_test(false, true);
  cond_test(false, false);

  cond2_test(true, true, true);
  cond2_test(true, false, true);
  cond2_test(false, true, true);
  cond2_test(false, true, false);
  cond2_test(false, false, true);

	writeln("Not Recursive fibo(35) = " ++ fibo(35));
	writeln("Recursion f(35) = " ++ f(35));
}
