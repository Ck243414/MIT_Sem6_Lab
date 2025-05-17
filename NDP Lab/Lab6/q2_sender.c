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

// Function to append the CRC to the data
void append_crc(char *data, unsigned int crc, int crc_length) {
    // Append the CRC as the last 2 bytes of the data
    data[strlen(data)] = (crc >> 8) & 0xFF;
    data[strlen(data) + 1] = crc & 0xFF;
}

int main() {
    int sock;
    struct sockaddr_in server_addr;
    char buffer[MAX_BUF];
    unsigned int crc;

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

    // Input the data from the user
    printf("Enter the data to transmit: ");
    fgets(buffer, MAX_BUF, stdin);
    buffer[strcspn(buffer, "\n")] = 0;  // Remove newline character

    // Calculate CRC for the data (CRC-12, CRC-16, CRC-CCITT)
    crc = calculate_crc((unsigned char*)buffer, strlen(buffer), CRC12_POLY, 12);
    append_crc(buffer, crc, 12);  // Append CRC-12 to the data

    // Send the data to the server
    send(sock, buffer, strlen(buffer) + 2, 0);  // Send data + CRC

    printf("Data sent with CRC-12: %s\n", buffer);

    // Close the socket
    close(sock);
    return 0;
}
