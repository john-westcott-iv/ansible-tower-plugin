# Jenkins Ansible Tower Plugin

This plugin gives you the ability to run [Ansible Tower](http://www.ansible.com/) jobs as a build step.

Jenkins Wiki page: TBD

## Configuration

On the Jenkins => Manage Jenkins => Configure System you will find an Ansible Tower section where you can add a connection to an Ansible Tower server.


## Pipeline support

Tower jobs can be executed from workflow scripts.
The towerServer and jobTemplate are the only required parameters.
G
```groovy  
steps {
        ansibleTower(
            towerServer: 'Prod Tower',
            jobTemplate: 'Simple Test',
            importTowerLogs: true,
            inventory: 'Demo Inventory',
            jobTags: '',
            limit: '',
            removeColor: false,
            verbose: true,
            credential: '',
            extraVars: '''---
my_var: "Jenkins Test"'''
        )
}
```


### Colorized Console Log

You need to install the [AnsiColor plugin](https://wiki.jenkins-ci.org/display/JENKINS/AnsiColor+Plugin) to output a colorized Ansible log.

```groovy
node {
    wrap([$class: 'AnsiColorBuildWrapper', colorMapName: "xterm"]) {
        ansibleTower(
            towerServer: 'Prod Tower',
            jobTemplate: 'Simple Test',
            importTowerLogs: true,
            inventory: 'Demo Inventory',
            jobTags: '',
            limit: '',
            removeColor: false,
            verbose: true,
            credential: '',
            extraVars: '''---
my_var: "Jenkins Test"'''
        )
    }
}
```

If you dont have AnsiColor or want to remove the color from the log set removeColor: true.
