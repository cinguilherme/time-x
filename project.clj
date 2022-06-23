(defproject org.clojars.cinguilherme/time-x "0.0.0"
  :description "library to offer basic Date Time functionality to clojure with the more robust java.time under the hood"
  :url "https://github.com/cinguilherme/time-x"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [prismatic/schema "1.3.0"]
                 [prismatic/schema-generators "0.1.5"]
                 [com.stuartsierra/component "1.1.0"]
                 [nubank/state-flow "5.14.1"]
                 [nubank/matcher-combinators "3.5.0"]]
  :plugins [[lein-cloverage "1.0.13"]
            [lein-shell "0.5.0"]
            [lein-ancient "0.6.15"]
            [lein-changelog "0.3.2"]]
  :profiles {:dev {:dependencies [[org.clojure/clojure "1.10.3"]
                                  [nubank/state-flow "5.14.1"]
                                  [nubank/matcher-combinators "3.5.0"]]}}
  :deploy-repositories [["releases" :clojars]]
  :aliases {"update-readme-version" ["shell" "sed" "-i" "s/\\\\[org\\.clojars\\.cinguilherme\\\\/time-x \"[0-9.]*\"\\\\]/[org\\.clojars\\.cinguilherme\\\\/time-x \"${:version}\"]/" "README.md"]}
  :release-tasks [["shell" "git" "diff" "--exit-code"]
                  ["change" "version" "leiningen.release/bump-version"]
                  ["change" "version" "leiningen.release/bump-version" "release"]
                  ["changelog" "release"]
                  ["update-readme-version"]
                  ["vcs" "commit"]
                  ["vcs" "tag"]
                  ["deploy"]
                  ["vcs" "push"]])
