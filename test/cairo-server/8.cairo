/WIDTH 400 def
/HEIGHT 400 def

/COUNT 8 def
/STRIPE WIDTH COUNT div def

/stripe { % cr r g b
  rgb set-source
  0 0 1 1 rectangle
  fill
  1 0 translate
} def

<< /width WIDTH /height HEIGHT >> surface context

STRIPE HEIGHT scale

0 0 0 stripe
0 0 1 stripe
1 0 0 stripe
1 0 1 stripe
0 1 0 stripe
0 1 1 stripe
1 1 0 stripe
1 1 1 stripe

copy-page

pop
