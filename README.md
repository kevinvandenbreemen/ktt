
# Libraries Used
## ktt
Repo to try out some development using ktor for Kotlin server-side coding

## Info on KTOR
* https://github.com/ktorio/ktor
* https://ktor.io/docs/welcome.html

## Info on SQLite
* https://github.com/xerial/sqlite-jdbc/tree/master
* https://github.com/xerial/sqlite-jdbc/blob/master/USAGE.md
* https://github.com/kevinvandenbreemen/sqlite-dao

## Info on Kotlinx.HTML
* https://github.com/Kotlin/kotlinx.html/wiki/

# Planned Features
## Macros
Macros will provide a syntax that looks as follows:
```
{@macro:MacroName}
```

Where ```@macro``` opens a new macro.  The colon is followed by the name of the macro type.  Macro types should be acquired using a registry of named macros rather than using class lookups by name.

### Providing arguments to macros
Arguments are provided using values specified with quotes, as in:

```
{@macro:Greeting msg="Hello to the world"}
```

### Why have a prefix etc when other wikis just have a curly brace and macro name?
At the moment I'd rather have an instantly recognizable prefix in the curly brace to allow me to put other things into this wiki software that involve curly braces...

### Adding Macros to the server
Macros can be added via the macro registry, like this:

```
WikiApplication.macroRegistry.register(myMacro)
```

Your ```myMacro``` object will need to be of a type that implements the ```Macro``` interface.