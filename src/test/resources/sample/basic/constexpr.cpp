#include <iostream>
#include <stdexcept>

constexpr int factorial(int n)
{
  return n <= 1 ? 1 : (n * factorial(n - 1));
}

template<int n>
struct constN
{
  constN() { std::cout << n << '\n'; }
};

int main()
{
  std::cout << "4! = " ;
  constN<factorial(4)> out1;

  constexpr double d1 = 2.0/1.0;

  std::cout << d1; 
}   
