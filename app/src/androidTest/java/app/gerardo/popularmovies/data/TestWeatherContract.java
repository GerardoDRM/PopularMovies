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

import android.net.Uri;
import android.test.AndroidTestCase;

/*
    Students: This is NOT a complete test for the WeatherContract --- just for the functions
    that we expect you to write.
 */
public class TestWeatherContract extends AndroidTestCase {

    /*
        Students: Uncomment this out to test your weather location function.
     */
    public void testBuildWeatherLocation() {
        Uri locationUri = MoviesContract.MovieEntry.buildMovieUri();
        assertNotNull("Error: Null Uri returned.  You must fill-in buildWeatherLocation in " +
                        "WeatherContract.",
                locationUri);
        assertEquals("Error: Movie location Uri doesn't match our expected result",
                locationUri.toString(),
                "content://app.gerardo.popularmovies/movie");
    }
}
