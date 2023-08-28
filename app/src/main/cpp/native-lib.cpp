#include <jni.h>
#include <string>
#include <android/log.h>
#include <fcntl.h>


// Define ARM64 syscall numbers (replace with actual values)
#define ARM64_OPENAT_SYSCALL 56

// Inline syscall for cc_openat
int cc_openat(int dirfd, const char *pathname, int flags, mode_t mode) {
    int fd;
    asm volatile (
    "mov x8, %1\n\t"       // syscall number
    "mov x0, %2\n\t"       // dirfd
    "mov x1, %3\n\t"       // pathname
    "mov x2, %4\n\t"       // flags
    "mov x3, %5\n\t"       // mode
    "svc #0x0\n\t"         // software interrupt to trigger syscall
    "mov %0, x0\n\t"       // store syscall return value
    : "=r" (fd)            // output
    : "r" (ARM64_OPENAT_SYSCALL), "r" (dirfd), "r" (pathname), "r" (flags), "r" (mode) // inputs
    : "x0", "x8", "memory" // clobbered registers
    );
    return fd;
}


extern "C"
JNIEXPORT void JNICALL
Java_live_rakan_detectemulator_MainActivity_test_1syscalls(JNIEnv *env, jobject thiz) {
    int fd = cc_openat(AT_FDCWD,"/proc/self/maps",O_RDONLY,0);
    __android_log_print(ANDROID_LOG_ERROR, "fd", "%d ", fd);

    if(fd == -1) {
        __android_log_print(ANDROID_LOG_ERROR, "File", "The file not opened and status = %d ", fd);
    }
    int isOpen = openat(AT_FDCWD,"/HACKERS_syscall",O_RDONLY);
    __android_log_print(ANDROID_LOG_ERROR, "isOpen", "%d ", isOpen);
    if(isOpen == -1) {
        __android_log_print(ANDROID_LOG_ERROR, "File", "The file not opened and status = %d ", isOpen);
    }


}