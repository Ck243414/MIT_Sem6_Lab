
// server.c
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <arpa/inet.h>

#define PORT 8080
#define MAX_BUF 1024

int main() {
    int server_fd, new_socket;
    struct sockaddr_in address;
    socklen_t addr_len = sizeof(address);
    char buffer[MAX_BUF];
    pid_t pid, ppid;

    // Display PID and PPID
    pid = getpid();
    ppid = getppid();
    printf("Server PID: %d, PPID: %d\n", pid, ppid);

    // Create socket
    if ((server_fd = socket(AF_INET, SOCK_STREAM, 0)) == 0) {
        perror("Socket failed");
        exit(EXIT_FAILURE);
    }

    address.sin_family = AF_INET;
    address.sin_addr.s_addr = INADDR_ANY;
    address.sin_port = htons(PORT);

    // Bind the socket to the address
    if (bind(server_fd, (struct sockaddr *)&address, sizeof(address)) < 0) {
        perror("Bind failed");
        exit(EXIT_FAILURE);
    }

    // Listen for incoming connections
    if (listen(server_fd, 3) < 0) {
        perror("Listen failed");
        exit(EXIT_FAILURE);
    }

    printf("Server listening on port %d...\n", PORT);

    // Accept incoming connection
    if ((new_socket = accept(server_fd, (struct sockaddr *)&address, &addr_len)) < 0) {
        perror("Accept failed");
        exit(EXIT_FAILURE);
    }

    printf("Client connected.\n");

    // Chat loop (full-duplex communication)
    while (1) {
        // Receive message from client
        memset(buffer, 0, MAX_BUF);
        read(new_socket, buffer, sizeof(buffer));
        printf("Client: %s\n", buffer);

        // Send message to client
        printf("Server: ");
        fgets(buffer, sizeof(buffer), stdin);
        write(new_socket, buffer, strlen(buffer));
        // Exit condition: if message is "exit", terminate the loop
        if (strncmp(buffer, "exit", 4) == 0) {
            break;
        }
    }

    printf("Closing connection with client.\n");
    close(new_socket);
    close(server_fd);
    return 0;
}
