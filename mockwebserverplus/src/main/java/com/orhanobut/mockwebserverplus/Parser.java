package com.orhanobut.mockwebserverplus;

import java.io.InputStream;

interface Parser {

  Fixture parse(InputStream inputStream);
}
