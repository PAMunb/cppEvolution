#include <iostream>
#include <map>

int main()
{
  std::map<std::string, int> map;
  map["hello"] = 1;
  map["world"] = 2;

  if (auto ret = map.insert({ "hello", 3 }); !ret.second)
    std::cout << "hello already exists with value " << ret.first->second << "\n";
  
  if (auto ret = map.insert({ "foo", 4 }); !ret.second)
    std::cout << "foo already exists with value " << ret.first->second << "\n";

  if(true) std::cout << "foo";
  
  return 0;
}
