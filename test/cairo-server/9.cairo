/WIDTH 400 def
/HEIGHT 400 def

/COUNT 6 def
/STRIPE WIDTH COUNT div def

/get6 { % cr i
  COLORS exch
  3 mul
  dup
  5 add
  1 exch
  { get exch } for
  pop
} def

/COLORS [
  1 0 0
  1 1 0
  0 1 0
  0 1 1
  0 0 1
  1 0 1
  1 0 0
] def

/stripe {
  get6
  6 index
  0 0 1 1 rectangle
  0 0 1 0 linear
  0 3 { 8 index } repeat 1 add-color-stop
  1 3 { 5 index } repeat 1 add-color-stop
  set-source fill
  7 { pop } repeat
  1 0 translate
} def


<< /width WIDTH /height HEIGHT >> surface context

STRIPE HEIGHT scale

0 1 COUNT 1 sub { stripe } for

copy-page

pop

