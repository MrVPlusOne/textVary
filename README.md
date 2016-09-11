# textVary
Helps you write better GRE/TOEFL essays by detecting your repeating phrases.

## What is textVary

[Introduction in English (英文介绍)](https://mrvplusone.github.io/textVary/)

[中文介绍 (Introduction in Chinese)](https://mrvplusone.github.io/textVary/index_zh.html)

## Download

[textVary-1.0.zip](https://github.com/MrVPlusOne/textVary/releases/download/v1.0/textVary-1.0.zip)

## How it works

To get statistics of phrase usage from the input:

  * TextVary first parses the essay into a sequence of *SentencePart*s, each *SentencePart* is composed of many *WordUsage*s
  * A *WordUsage* contains information about a word's original form and position in the essay, as well as the stem of that word. The stem is used to match words and phrases in later stages.
    * The parser is written in the parser-combinator library [FastParse](https://github.com/lihaoyi/fastparse)
    * The stemming strategy makes use of a combination of Porter's algorithm and an irregular word list. The scala implementation of Porter's algorithm was copied from [here](https://github.com/scalanlp/chalk/blob/master/src/main/scala/chalk/text/analyze/PorterStemmer.scala)
  * TextVary stores a collection of set phrases as a Finite State Automaton(FSA). The words in those phrases are reduced to their stem forms.
  * To detect set phrases, textVary uses a greedy algorithm, trying to find the longest phrases accepted by the FSA. A set phrase can not span across the boundaries of *SentencePart*s.

To turn the statistics into visual feedback:

  * The UI is implemented in a Reactive Programming (RP) style.
    ```
    The current input ~> usage statistics |
                     UI input information | ~> visual data >> pain visual elements on top of the input
    ```

  * As the frequency of a word or phrase increases, its background color varies from a translucent yellow to a transparent red. In this way, if a usage appears again and again in an essay, its highlight will eventually become unnoticeable. This is the desired behaviour because it helps the user focus on avoiding using the same expression twice or the third time, but ignore the repeating use of topic words or key words.
   * ![2 cars](https://github.com/MrVPlusOne/textVary-storage/blob/master/2cars.png?raw=true)
   * ![4 cars](https://github.com/MrVPlusOne/textVary-storage/blob/master/4cars.png?raw=true)
   * ![8 cars](https://github.com/MrVPlusOne/textVary-storage/blob/master/8cars.png?raw=true)


## Licence

The MIT License (MIT)

Copyright (c) 2016 Jiayi Wei (wjydzh1@163.com)

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.