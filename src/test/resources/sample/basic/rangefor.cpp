#include <initializer_list>
#include <iostream>

using namespace std;

int main() {
  int sum = 0; 
  for ( auto i : {1 ,2 ,3 ,5 ,8} ) sum += i;

  cout << sum; 
}
