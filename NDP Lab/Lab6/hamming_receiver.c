#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <arpa/inet.h>

#define PORT 8080
#define MAX_BUF 1024

// Function to calculate the parity bit
int calculate_parity(int data[], int check_pos, int data_length) {
    int parity = 0;
    for (int i = 0; i < data_length; i++) {
        if ((i + 1) & check_pos) {  // If this bit is checked by the parity bit
            parity ^= data[i];  // XOR the data bit
        }
    }
    return parity;
}

// Function to check the parity and detect errors
int check_parity(int encoded[], int check_pos, int data_length) {
    int parity = calculate_parity(encoded, check_pos, data_length);
    return parity;
}

// Function to decode Hamming (7,4) and detect/correct errors
void decode_hamming(int encoded[]) {
    int error_position = 0;
    // Check each parity bit (positions 1, 2, 4)
    if (check_parity(encoded, 1, 7)) error_position |= 1;
    if (check_parity(encoded, 2, 7)) error_position |= 2;
    if (check_parity(encoded, 4, 7)) error_position |= 4;

    // If error_position is non-zero, correct the error
    if (error_position) {
        printf("Error detected at position %d\n", error_position);
        encoded[error_position - 1] = !encoded[error_position - 1];  // Flip the erroneous bit
        printf("Error corrected.\n");
    } else {
        printf("No error detected.\n");
    }

    // Output the corrected data
    printf("Corrected Data: ");
    printf("%d%d%d%d\n", encoded[2], encoded[4], encoded[5], encoded[6]);
}

int main() {
    int server_socket, client_socket;
    struct sockaddr_in server_addr, client_addr;
    socklen_t addr_len = sizeof(client_addr);
    char buffer[MAX_BUF];
    int encoded[7];

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

    // Receive the data from the client
    int bytes_received = recv(client_socket, buffer, 7, 0);
    buffer[bytes_received] = '\0';  // Null-terminate the received data

    // Convert received string to integer array
    for (int i = 0; i < 7; i++) {
        encoded[i] = buffer[i] - '0';  // Convert char to integer (0 or 1)
    }

    // Decode and check for errors
    decode_hamming(encoded);

    // Close the client and server sockets
    close(client_socket);
    close(server_socket);
    return 0;
}
