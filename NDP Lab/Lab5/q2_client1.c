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
    char str1[MAX_BUF], str2[MAX_BUF];

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

    // Prompt user for two strings
    printf("Enter first string: ");
    fgets(str1, MAX_BUF, stdin);
    str1[strcspn(str1, "\n")] = 0;  // Remove newline character

    printf("Enter second string: ");
    fgets(str2, MAX_BUF, stdin);
    str2[strcspn(str2, "\n")] = 0;  // Remove newline character

    // Prepare the message to send to the server
    snprintf(buffer, MAX_BUF, "%s %s", str1, str2);

    // Send the strings to the server
    send(sock, buffer, strlen(buffer), 0);

    // Receive result from the server
    int bytes_received = recv(sock, buffer, MAX_BUF, 0);
    buffer[bytes_received] = '\0';  // Null-terminate the received string

    // Display the result
    printf("Server response: %s\n", buffer);

    // Close the socket
    close(sock);
    return 0;
}
