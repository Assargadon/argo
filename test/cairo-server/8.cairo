/WIDTH 400 def
/HEIGHT 400 def

<< /width WIDTH /height HEIGHT >> surface context

/COUNT 8 def
/STRIPE WIDTH COUNT div def

% def, U Y NO WORK for me?
%/stripe {
%  0 0 1 1 rectangle
%  rgb set-source fill
%  1 0 translate
%} def

STRIPE HEIGHT scale

0 0 1 1 rectangle
0 0 0 rgb set-source fill
1 0 translate

0 0 1 1 rectangle
0 0 1 rgb set-source fill
1 0 translate

0 0 1 1 rectangle
0 1 0 rgb set-source fill
1 0 translate

0 0 1 1 rectangle
0 1 1 rgb set-source fill
1 0 translate

0 0 1 1 rectangle
1 0 0 rgb set-source fill
1 0 translate

0 0 1 1 rectangle
1 0 1 rgb set-source fill
1 0 translate

0 0 1 1 rectangle
1 1 0 rgb set-source fill
1 0 translate

0 0 1 1 rectangle
1 1 1 rgb set-source fill
1 0 translate

% 0 0 0 stripe
% 0 0 1 stripe
% 0 1 0 stripe
% 0 1 1 stripe
% 1 0 0 stripe
% 1 0 1 stripe
% 1 1 0 stripe
% 1 1 1 stripe

