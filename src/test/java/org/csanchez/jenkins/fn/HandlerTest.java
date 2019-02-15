/*
 * The MIT License
 *
 * Copyright (c) 2019, CloudBees, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.csanchez.jenkins.fn;

import static org.junit.Assert.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import com.fnproject.fn.testing.FnResult;
import com.fnproject.fn.testing.FnTestingRule;
import com.google.gson.Gson;

public class HandlerTest {

    @Rule
    public final FnTestingRule testing = FnTestingRule.createDefault();

    @Ignore
    @Test
    public void test() throws Exception {
        File tmpDir = new File(System.getProperty("user.dir") + "/target/tmp");
        tmpDir.mkdirs();
        System.setProperty("app.root", System.getProperty("user.dir") + "/target/app");
        System.setProperty("tmp.dir", tmpDir.getAbsolutePath());
        System.setProperty("git.path", "/usr/local/bin/git");
        GitHubPayload request = new GitHubPayload();
        request.setAfter("master");
        Map<String, Object> repository = new HashMap<>();
        repository.put("clone_url", "https://github.com/carlossg/jenkinsfile-runner-lambda-example.git");
        request.setRepository(repository);

        testing.givenEvent().withBody(new Gson().toJson(request)).enqueue();
        testing.thenRun(Handler.class, "handleRequest");

        FnResult result = testing.getOnlyResult();
        assertEquals("{\"exitCode\":0,\"message\":\"Finished\"}", result.getBodyAsString());
    }

}
