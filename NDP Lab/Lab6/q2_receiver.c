#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <arpa/inet.h>

#define PORT 8080
#define MAX_BUF 1024

// CRC-12 Polynomial: 0x80F
#define CRC12_POLY 0x80F
// CRC-16 Polynomial: 0x8005
#define CRC16_POLY 0x8005
// CRC-CCITT Polynomial: 0x11021
#define CRC_CCITT_POLY 0x11021

// Function to calculate CRC for a given polynomial
unsigned int calculate_crc(unsigned char *data, int length, unsigned int polynomial, int crc_length) {
    unsigned int crc = 0;
    unsigned int bitmask = (1 << (crc_length - 1));
    
    // Process each byte of data
    for (int i = 0; i < length; i++) {
        crc ^= (data[i] << (crc_length - 8));  // Shift byte into CRC register

        // Process each bit
        for (int j = 8; j > 0; j--) {
            if (crc & bitmask) {
                crc = (crc << 1) ^ polynomial;
            } else {
                crc <<= 1;
            }
        }
    }

    return crc & ((1 << crc_length) - 1);  // Mask out excess bits
}

// Function to check if CRC matches
int check_crc(unsigned char *data, int length, unsigned int received_crc, unsigned int polynomial, int crc_length) {
    unsigned int crc = calculate_crc(data, length, polynomial, crc_length);
    return crc == received_crc;  // Return 1 if CRC matches, 0 if corrupted
}

int main() {
    int server_socket, client_socket;
    struct sockaddr_in server_addr, client_addr;
    socklen_t addr_len = sizeof(client_addr);
    char buffer[MAX_BUF];
    unsigned int received_crc;
    
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

    // Receive the data and CRC from the client
    int bytes_received = recv(client_socket, buffer, MAX_BUF, 0);
    buffer[bytes_received] = '\0';  // Null-terminate the received data

    // Extract received CRC (assuming the last 2 bytes are CRC)
    received_crc = (buffer[bytes_received - 2] << 8) | buffer[bytes_received - 1];

    // Remove CRC from the data buffer
    buffer[bytes_received - 2] = '\0';  // Null-terminate the data part

    // Check for CRC corruption (CRC12, CRC16, CRC-CCITT)
    if (check_crc((unsigned char*)buffer, bytes_received - 2, received_crc, CRC12_POLY, 12)) {
        printf("CRC-12: Data is valid.\n");
    } else {
        printf("CRC-12: Data is corrupt.\n");
    }

    if (check_crc((unsigned char*)buffer, bytes_received - 2, received_crc, CRC16_POLY, 16)) {
        printf("CRC-16: Data is valid.\n");
    } else {
        printf("CRC-16: Data is corrupt.\n");
    }

    if (check_crc((unsigned char*)buffer, bytes_received - 2, received_crc, CRC_CCITT_POLY, 16)) {
        printf("CRC-CCITT: Data is valid.\n");
    } else {
        printf("CRC-CCITT: Data is corrupt.\n");
    }

    // Close the client and server sockets
    close(client_socket);
    close(server_socket);
    return 0;
}
