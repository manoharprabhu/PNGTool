# PNGTool
[![codecov](https://codecov.io/gh/manoharprabhu/PNGTool/branch/main/graph/badge.svg?token=J1HETR3SQS)](https://codecov.io/gh/manoharprabhu/PNGTool)

A PNG image parser

https://en.wikipedia.org/wiki/Portable_Network_Graphics

Usage
---------------

    val tool = PNGTool(file)
Thows Exception if the file is not a valid PNG, or fails validation.

APIs
---------------
<br />

    getAllChunkTypes(): List<String>
Returns all the chunk types in the PNG.
<br />
<br />

    getChunks(type: String): List<Chunk>
Return chunks of the mentioned type.
