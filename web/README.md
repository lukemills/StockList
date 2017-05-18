# StockList Web Application

### Primary Contributor: Erik Rutledge

This web application is built with [Polymer](https://www.polymer-project.org/1.0/).

### Usage

The commands found below are the steps to install all required dependencies and serve the application. Full functionality, however, is reliant on the backend server being up and running. This in controlled and maintained by Colin.

##### Live Preview

At the time of this writing, you can visit [this link](http://hlr710vm.homelinux.com:81/) to view a live version of the application. Again, if this is not available, please contact Colin.

##### Browser

It was discovered through testing that there are differences in functionality across different browsers. Safari appears to work the best and Firefox is also a good option. Note that Chrome suffers from a CSS issue which disables a number of button operations.

### Setup

##### Prerequisites

First, install [Polymer CLI](https://github.com/Polymer/polymer-cli) using
[npm](https://www.npmjs.com) (we assume you have pre-installed [node.js](https://nodejs.org)).

    npm install -g polymer-cli

##### Clone project from GitLab

    git clone https://gitlab.com/UTK_CS340_SP17/StockList.git
    cd StockList/web
    bower install

### Start the development server

    polymer serve --open

### Build

This command performs HTML, CSS, and JS minification on the application
dependencies, and generates a service-worker.js file with code to pre-cache the
dependencies based on the entrypoint and fragments specified in `polymer.json`.
The minified files are output to the `build/unbundled` folder, and are suitable
for serving from a HTTP/2+Push compatible server.

In addition the command also creates a fallback `build/bundled` folder,
generated using fragment bundling, suitable for serving from non
H2/push-compatible servers or to clients that do not support H2/Push.

    polymer build

### Preview the build

This command serves the minified version of the app at `http://127.0.0.1:8081`
in an unbundled state, as it would be served by a push-compatible server:

    polymer serve build/default

### Run tests

This command will run [Web Component Tester](https://github.com/Polymer/web-component-tester)
against the browsers currently installed on your machine:

    polymer test

### Adding a new view

You can extend the app by adding more views that will be demand-loaded
e.g. based on the route, or to progressively render non-critical sections of the
application. Each new demand-loaded fragment should be added to the list of
`fragments` in the included `polymer.json` file. This will ensure those
components and their dependencies are added to the list of pre-cached components
and will be included in the `bundled` build.
