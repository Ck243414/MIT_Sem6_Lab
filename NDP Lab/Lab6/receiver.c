#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <arpa/inet.h>

#define PORT 8080
#define MAX_BUF 1024

// Function to check if the received data is valid based on even or odd parity
int check_parity(char *data, int expected_parity) {
    int count_ones = 0;
    
    // Count the number of 1s in the binary data
    for (int i = 0; data[i] != '\0'; i++) {
        if (data[i] == '1') {
            count_ones++;
        }
    }

    // Check if the parity matches the expected value (odd or even)
    if (expected_parity == 0) {
        // Even parity: number of 1s must be even
        return (count_ones % 2 == 0) ? 1 : 0; // 1 means valid, 0 means corrupt
    } else {
        // Odd parity: number of 1s must be odd
        return (count_ones % 2 != 0) ? 1 : 0; // 1 means valid, 0 means corrupt
    }
}

int main() {
    int server_socket, client_socket;
    struct sockaddr_in server_addr, client_addr;
    socklen_t addr_len = sizeof(client_addr);
    char buffer[MAX_BUF];
    int expected_parity = 0;  // 0 for even, 1 for odd (you can change this based on your requirement)

    // Create server socket
    if ((server_socket = socket(AF_INET, SOCK_STREAM, 0)) == -1) {
        perror("Socket creation failed");
        exit(EXIT_FAILURE);
    }

    server_addr.sin_family = AF_INET;
    server_addr.sin_addr.s_addr = INADDR_ANY;
    server_addr.sin_port = htons(PORT);

    // Bind the socket
    if (bind(server_socket, (struct sockaddr *)&server_addr, sizeof(server_addr)) == -1) {
        perror("Bind failed");
        close(server_socket);
        exit(EXIT_FAILURE);
    }

    // Listen for incoming connections
    if (listen(server_socket, 5) == -1) {
        perror("Listen failed");
        close(server_socket);
        exit(EXIT_FAILURE);
    }

    printf("Server is waiting for a connection on port %d...\n", PORT);

    // Accept the client connection
    client_socket = accept(server_socket, (struct sockaddr *)&client_addr, &addr_len);
    if (client_socket == -1) {
        perror("Accept failed");
        close(server_socket);
        exit(EXIT_FAILURE);
    }

    printf("Connected to client %s:%d\n", inet_ntoa(client_addr.sin_addr), ntohs(client_addr.sin_port));

    // Receive the binary data from the client
    int bytes_received = recv(client_socket, buffer, MAX_BUF, 0);
    buffer[bytes_received] = '\0';  // Null-terminate the received data

    printf("Received binary data: %s\n", buffer);

    // Check the parity of the received data
    if (check_parity(buffer, expected_parity)) {
        printf("Data is valid.\n");
    } else {
        printf("Data is corrupt.\n");
    }

    // Close the client connection
    close(client_socket);
    // Close the server socket
    close(server_socket);
    return 0;
}
