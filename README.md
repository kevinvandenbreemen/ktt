# Getting Started
## Up and Running

### Running with Gradle (FatJar)
Once you've checked out the project simply run the ```FatJar``` gradle task to build ktt.jar, a jar containing the project and its dependencies.  Then you can run the program as follows:
```
java -jar ktt.jar
```

### From inside your IDE
You can start the wiki by running the ```WikiApplication``` class.  The server will start on port 8080.  Data will be stored in database file main.db in the same directory.

# Main Features
## Macros
Macros have a syntax that looks as follows:
```
{@macro:MacroName}
```

Where ```@macro``` opens a new macro.  The colon is followed by the name of the macro type.  Macro types should be acquired using a registry of named macros rather than using class lookups by name.

### Providing arguments to macros
Arguments are provided using values specified with quotes, as in:

```
{@macro:Greeting msg="Hello to the world"}
```

### Adding Macros to the server

Define a class that implements the ```Macro``` interface.  Then add it to the  macro registry:

```
WikiApplication.macroRegistry.register(myMacro)
```

At the moment this is done in the MainServer.kt file.

## Custom CSS
You can customize the css for the wiki by updating the styling for the appropriate section of each page on the main screen (by going to ```/``` on the wiki).  Note that for now css classes are used to control most of the styling.  So, for example, the body of a wiki entry being displayed falls under css class ```.wiki_entry```.  You can extend this system by adding more classes (see the ```Classes``` class).  If you wish to make a given css class customizable simply add an entry for it to the StylesheetType enum, and provide the css class you wish to make customizable as constructor argument, as in:

```
enum class StylesheetType(val cssClass: String) {
    WikiEntry(wikiEntry),
    ControlPanel(controlPanel)
}
```

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

## Info on the Markdown Lib used
https://github.com/JetBrains/markdown

### General info on Markdown
https://www.markdownguide.org/cheat-sheet/

# Future Feature Ideas
## CSS stored in the database
* Right now the css is just hard-coded in the ```Styling``` class.  
* Provide a way of letting the user enter their own CSS on a page in the wiki that then styles the wiki.