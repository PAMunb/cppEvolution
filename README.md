## CPPEvolutio-analysis

This program collects information about the adoption of
"modern" C++ features in Git repositories. 

### Motivation

"After C++11 was beginning to see serious use, I started taking small unscientific surveys as I
traveled widely to talk with C++ users: Which C++11 features do you most like? The top three
were invariably" [1]

   * *auto*
   * *lambdas*
   * *range for*
   
[1] Bjarne Stroustrup: Thriving in a crowded and changing world: C++ 2006-2020. 70:1-70:168.
The History of Programming Languages (2020). 

So, we want to understand how and to which extent C++ developers adopt new language
features (available from C++11 and above). We collect frequency of C++ feature
adoption as well as historical trends observing the Git history of the projects.


### Build

Clone the repository and then execute:

   * `$ ./install-deps.sh`
   * `mvn mvn clean compile assembly:single`

This will export a runnable JAR file named `CPPEvolution-analysis.jar` into the target project.

### Run

To execute the program, just run:

   * `cd target`
   * java -jar CPPEvolution-analysis.jar <path-to-the-git-repos> 

### List of collect C++ features

   [x] auto and decltype
   [x] Lambda Expressions
   [X] range-for
   [ ] nullptr
   [ ] constexpr
   [ ] override and final
   [ ] enum classes
   [ ] attributes
   [ ] noexcept