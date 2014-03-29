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
     _
    / \
   / _ \
  / ___ \
 /_/   \_\

=> nil
```

Rendering whole strings is just as easy:

```clojure
(println
  (clojure.string/join
    \newline
    (render flf "Hello, world!")))
  _   _          _   _                                           _       _   _
 | | | |   ___  | | | |   ___         __      __   ___    _ __  | |   __| | | |
 | |_| |  / _ \ | | | |  / _ \        \ \ /\ / /  / _ \  | '__| | |  / _` | | |
 |  _  | |  __/ | | | | | (_) |  _     \ V  V /  | (_) | | |    | | | (_| | |_|
 |_| |_|  \___| |_| |_|  \___/  ( )     \_/\_/    \___/  |_|    |_|  \__,_| (_)
                                |/
=> nil
```

For convenience sake, you can also use the `render-to-string` function:

```clojure
(println (render-to-string flf "w00t!"))
              ___     ___    _     _
 __      __  / _ \   / _ \  | |_  | |
 \ \ /\ / / | | | | | | | | | __| | |
  \ V  V /  | |_| | | |_| | | |_  |_|
   \_/\_/    \___/   \___/   \__| (_)

=> nil
```

Please note that currently, clj-figlet only supports the 'Full Size' layout mode as detailed in the FIGfont
specifications. The 'Fitting Only' and 'Smushing' layout modes will be added in the future.

## License

Distributed under the the MIT License. See LICENSE for more details.
