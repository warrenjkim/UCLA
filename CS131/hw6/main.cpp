#include <iostream>
#include <string>
#include "kontainer.hpp"

int main ()
{
  std::cout << "hello world" << std::endl;
  Kontainer<int> integers;

  integers.add(10);
  integers.add(20);
  integers.add(2);

  std::cout << integers.minimum_element() << std::endl;

  Kontainer<std::string> strings;

  strings.add("hello");
  strings.add("abc");
  strings.add("lmnop");

  std::cout << strings.minimum_element() << std::endl;

  Kontainer<double> doubles;

  doubles.add(10.0);
  doubles.add(12.32);
  doubles.add(0.1029320);
  doubles.add(0.000139);

  std::cout << doubles.minimum_element() << std::endl;
  return 0;
}
