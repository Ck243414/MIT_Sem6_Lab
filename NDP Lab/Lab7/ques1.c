#include <stdio.h>
#include <stdlib.h>

#define MAX_BUCKET_SIZE 10  // Maximum size of the bucket in bytes
#define OUTGOING_RATE 1     // Rate at which data is leaked (1 byte per second)

// Function to simulate the leaky bucket algorithm
void leakyBucket(int *packetTimes, int packetCount) {
    int bucket = 0; // current size of the bucket (in bytes)
    int time = 0;   // current time
    int conforming = 0, nonConforming = 0; // counts of conforming and non-conforming packets
    
    printf("Time\tAction\tBucket Size\tPacket Arrival\n");
    for (int i = 0; i < packetCount; i++) {
        // Leak the bucket by 1 byte for every second
        if (time < packetTimes[i]) {
            int leakTime = packetTimes[i] - time;
            bucket -= leakTime * OUTGOING_RATE;
            if (bucket < 0) {
                bucket = 0;
            }
            time = packetTimes[i];
        }

        // Now, handle the packet arrival at time packetTimes[i]
        if (bucket + 4 <= MAX_BUCKET_SIZE) {
            // Conforming packet: there's enough space in the bucket
            bucket += 4;
            conforming++;
            printf("%d\tIn\t%d\tConforming\n", time, bucket);
        } else {
            // Non-conforming packet: no space in the bucket, so drop the packet
            nonConforming++;
            printf("%d\tIn\t%d\tNon-Conforming (Dropped)\n", time, bucket);
        }
    }

    // Print the final counts of conforming and non-conforming packets
    printf("\nTotal conforming packets: %d\n", conforming);
    printf("Total non-conforming packets: %d\n", nonConforming);
}

int main() {
    // The time intervals at which packets arrive (in seconds)
    int packetTimes[] = {1, 2, 3, 5, 6, 8, 11, 12, 15, 16, 19};
    int packetCount = sizeof(packetTimes) / sizeof(packetTimes[0]);

    // Call the leaky bucket function
    leakyBucket(packetTimes, packetCount);

    return 0;
}
