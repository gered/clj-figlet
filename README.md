# clj-figlet

A Clojure library to generate text banners in an 'ASCII Art' style using FIGlet fonts, similar to other
[FIGlet programs](http://www.figlet.org/).

## Usage

!["clj-figlet version"](https://clojars.org/clj-figlet/latest-version.svg)

You'll need to download one or more FIGlet fonts first. Probably the best place to get these is the
[FIGlet font database](http://www.figlet.org/fontdb.cgi). Once you've got some ready loading them up is easy:

```clojure
(use 'clj-figlet.core)

(def flf (load-flf "/Users/gered/roman.flf"))
```

`load-flf` returns a map which you will need to pass as the `flf` argument to any of the render functions.
Some useful properties of flf fonts are present in the `:header` key.

Rendering characters, strings, multi-line strings are all easy. For example:

```clojure
(render-char flf \A)
```

`render-char`, `render-line` and `render` all return the rendered output as sequences of strings. Each string
in the sequence corresponds to a line of rendered output (a line of 'pixels' if you like). The number of strings
will be equal to the height of the font (which is stored in the flf header under `:height`).

If we just cared about getting some nice output right away (e.g. for use when outputting to the console):

```clojure
(println
  (clojure.string/join
    \newline
    (render-char flf \A)))
      .o.
     .888.
    .8"888.
   .8' `888.
  .88ooo8888.
 .8'     `888.
o88o     o8888o



=> nil
```

Rendering whole strings is just as easy:

```clojure
(println
  (clojure.string/join
    \newline
    (render flf "Hello, world!")))
ooooo   ooooo           oooo  oooo                                                       oooo        .o8  .o.
`888'   `888'           `888  `888                                                       `888       "888  888
 888     888   .ooooo.   888   888   .ooooo.         oooo oooo    ooo  .ooooo.  oooo d8b  888   .oooo888  888
 888ooooo888  d88' `88b  888   888  d88' `88b         `88. `88.  .8'  d88' `88b `888""8P  888  d88' `888  Y8P
 888     888  888ooo888  888   888  888   888          `88..]88..8'   888   888  888      888  888   888  `8'
 888     888  888    .o  888   888  888   888 .o.       `888'`888'    888   888  888      888  888   888  .o.
o888o   o888o `Y8bod8P' o888o o888o `Y8bod8P' Y8P        `8'  `8'     `Y8bod8P' d888b    o888o `Y8bod88P" Y8P
                                               '


=> nil
```

For convenience sake, you can also use the `render-to-string` function:

```clojure
(println (render-to-string flf "w00t!"))
                   .oooo.     .oooo.       .   .o.
                  d8P'`Y8b   d8P'`Y8b    .o8   888
oooo oooo    ooo 888    888 888    888 .o888oo 888
 `88. `88.  .8'  888    888 888    888   888   Y8P
  `88..]88..8'   888    888 888    888   888   `8'
   `888'`888'    `88b  d88' `88b  d88'   888 . .o.
    `8'  `8'      `Y8bd8P'   `Y8bd8P'    "888" Y8P



=> nil
```

Please note that currently, clj-figlet only supports the 'Full Size' layout mode as detailed in the FIGfont
specifications. The 'Fitting Only' and 'Smushing' layout modes will be added in the future.

## License

Distributed under the the MIT License. See LICENSE for more details.
