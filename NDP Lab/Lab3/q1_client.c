// client.c
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <arpa/inet.h>

#define PORT 8080
#define MAX_BUF 1024

int main() {
    int sock = 0;
    struct sockaddr_in server_addr;
    char buffer[MAX_BUF];
    pid_t pid, ppid;

    // Display PID and PPID
    pid = getpid();
    ppid = getppid();
    printf("Client PID: %d, PPID: %d\n", pid, ppid);

    // Create socket
    if ((sock = socket(AF_INET, SOCK_STREAM, 0)) < 0) {
        perror("Socket creation failed");
        return -1;
    }

    server_addr.sin_family = AF_INET;
    server_addr.sin_port = htons(PORT);

    // Convert IP address from text to binary form
    if (inet_pton(AF_INET, "127.0.0.1", &server_addr.sin_addr) <= 0) {
        perror("Invalid address or Address not supported");
        return -1;
    }

    // Connect to the server
    if (connect(sock, (struct sockaddr *)&server_addr, sizeof(server_addr)) < 0) {
        perror("Connection failed");
        return -1;
    }

    printf("Connected to server.\n");

    // Chat loop (full-duplex communication)
    while (1) {
        // Send message to server
        printf("Client: ");
        fgets(buffer, sizeof(buffer), stdin);
        write(sock, buffer, strlen(buffer));

        // Exit condition: if message is "exit", terminate the loop
        if (strncmp(buffer, "exit", 4) == 0) {
            break;
        }

        // Receive message from server
        memset(buffer, 0, MAX_BUF);
        read(sock, buffer, sizeof(buffer));
        printf("Server: %s\n", buffer);
    }

    printf("Closing connection to server.\n");
    close(sock);
    return 0;
}
