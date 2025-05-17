#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <arpa/inet.h>

#define PORT 8080
#define MAX_BUF 1024

int main() {
    int sock;
    struct sockaddr_in server_addr;
    char buffer[MAX_BUF];

    // Create socket
    if ((sock = socket(AF_INET, SOCK_STREAM, 0)) == -1) {
        perror("Socket creation failed");
        exit(EXIT_FAILURE);
    }

    // Set up server address
    server_addr.sin_family = AF_INET;
    server_addr.sin_port = htons(PORT);
    server_addr.sin_addr.s_addr = inet_addr("127.0.0.1"); // Localhost

    // Connect to the server
    if (connect(sock, (struct sockaddr *)&server_addr, sizeof(server_addr)) == -1) {
        perror("Connection failed");
        close(sock);
        exit(EXIT_FAILURE);
    }

    // Input the binary data from the user
    printf("Enter binary data (e.g., 110101): ");
    fgets(buffer, MAX_BUF, stdin);
    buffer[strcspn(buffer, "\n")] = 0;  // Remove newline character

    // Send the binary data to the server
    send(sock, buffer, strlen(buffer), 0);

    printf("Data sent: %s\n", buffer);

    // Close the socket
    close(sock);
    return 0;
}
