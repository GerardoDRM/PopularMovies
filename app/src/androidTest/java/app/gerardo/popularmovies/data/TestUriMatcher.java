/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package app.gerardo.popularmovies.data;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

/*
    This class test the URI Matcher
 */
public class TestUriMatcher extends AndroidTestCase {
    // content://com.example.android.sunshine.app/movie"
    private static final Uri TEST_MOVIE_DIR = MoviesContract.MovieEntry.CONTENT_URI;

    public void testUriMatcher() {
        UriMatcher testMatcher = MovieProvider.buildUriMatcher();
        assertEquals("Error: The MOVIE URI was matched incorrectly.",
                testMatcher.match(TEST_MOVIE_DIR), MovieProvider.MOVIE);
    }
}
