package jenkins.plugins.ansible_tower.util;

public class TowerLogger {
    private boolean debugging = false;
    public void setDebugging(boolean debugging) { this.debugging = debugging; }
    public void logMessage(String message) {
        if(debugging) {
            writeMessage(message);
        }
    }

    public static void writeMessage(String message) {
        System.out.println("[Ansible-Tower] "+ message);
    }
}
