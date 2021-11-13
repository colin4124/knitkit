# knitkit

Syntax is close to [chisel3](https://github.com/chipsalliance/chisel3), without [FIRRTL](https://github.com/chipsalliance/firrtl) optimizing and generating readable Verilog code faster.

## Quick Install

```shell
$ pip3 install knitkit
$ knitkit create hello_knitkit
$ cd hello_knitkit
$ make verilog
```

Source code in `hw/knitkit/src/Mux2.scala` and generated in `builds/Mux2.v`.

## Install in offline environment

Download [knitkit-py](https://github.com/colin4124/knitkit-py/releases/download/v0.3/knitkit-0.3-py3-none-any.whl) and then:

```shell
$ pip3 install --user knitkit-0.3-py3-none-any.whl
```

The same as quick install.

## Install

Build by [SBT](https://www.scala-sbt.org/) or [Mill](https://com-lihaoyi.github.io/mill/) locally. If just have a look, use [Scastie](https://scastie.scala-lang.org/) online.

### SBT

1. Create `hello_knitkit` project directory.

```shell
$ mkdir -p hello_knitkit/src/main/scala
$ cd hello_knitkit
$ touch build.sbt
```

2. Create SBT configuration as below:

```
lazy val hello = (project in file("."))
  .settings(
    name := "HelloKnitkit",
    scalaVersion := "2.13.6",
    libraryDependencies += "io.github.colin4124" %% "knitkit" % "0.3.0",
  )
```

3. Write `Knitkit` source code:

```shell
$ touch src/main/scala/Mux2.scala
```

```scala
import knitkit._

class Mux2 extends RawModule {
  val sel = IO(Input(UInt(1.W)))
  val in0 = IO(Input(UInt(1.W)))
  val in1 = IO(Input(UInt(1.W)))
  val out = IO(Output(UInt(1.W)))

  out := (sel & in1) | (~sel & in0)
}

object Main extends App {
  Driver.execute(() => new Mux2, args(0))
}
```

4. Run SBT, the first argument is generated directory, here is `builds`.

```shell
$ sbt "run builds"
```

5. Check generated verilog file `builds/Mux2.v`:

```verilog
module Mux2 (
  input  sel,
  input  in0,
  input  in1,
  output out
);
  assign out = (sel & in1) | ((~ sel) & in0);
endmodule
```


### Mill

1. Create `hello_knitkit` project directory.

```shell
$ mkdir -p hello_knitkit/src
$ cd hello_knitkit
$ touch build.sc
```

2. Create Mill configuration as below:

```
import mill._, scalalib._
  
object hello extends ScalaModule {
  def scalaVersion = "2.13.6"
  def millSourcePath = super.millSourcePath / ammonite.ops.up

  def ivyDeps = Agg(
    ivy"io.github.colin4124::knitkit:0.3.0",
  )
}
```

3. Put source code `Mux2.scala` to `hello_knitkit/src/` directory.

4. Run Mill, `hello` is the `object` name in `build.sc` defined, `builds` is the first argument where verilog generated.

```shell
$ mill hello.run builds
```

5. Check generated verilog file `builds/Mux2.v`.

### Online Scastie

1. Open  [Scastie](https://scastie.scala-lang.org/) website.

2. Click `Build Settings` in left sidebar.

3. Choose `scala 2` in `Target`, `2.13.6` in `Scala Version`, search `knitkit` in `Libraries`.

4. Click `Editor` in left sidebar, wirte down:

```scala
import knitkit._
  
class Mux2 extends RawModule {
  override def desiredName = "Mux2"
  val sel = IO(Input(UInt(1.W)))
  val in0 = IO(Input(UInt(1.W)))
  val in1 = IO(Input(UInt(1.W)))
  val out = IO(Output(UInt(1.W)))

  out := (sel & in1) | (~sel & in0)
}
Driver.genVerilog(() => new Mux2)
```

5. Here is the [exmple](https://scastie.scala-lang.org/mHbWcGrASjKvJfrGEBKmSA)
