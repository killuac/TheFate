/**
 * Autogenerated by Thrift
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 */

#import <Foundation/Foundation.h>

#import "TProtocol.h"
#import "TApplicationException.h"
#import "TProtocolUtil.h"
#import "TProcessor.h"


@interface ThriftEvictUserFromRoomRequest : NSObject <NSCoding> {
  int32_t __zoneId;
  int32_t __roomId;
  NSString * __userName;
  NSString * __reason;
  BOOL __ban;
  int32_t __duration;

  BOOL __zoneId_isset;
  BOOL __roomId_isset;
  BOOL __userName_isset;
  BOOL __reason_isset;
  BOOL __ban_isset;
  BOOL __duration_isset;
}

#if TARGET_OS_IPHONE || (MAC_OS_X_VERSION_MAX_ALLOWED >= MAC_OS_X_VERSION_10_5)
@property (nonatomic, getter=zoneId, setter=setZoneId:) int32_t zoneId;
@property (nonatomic, getter=roomId, setter=setRoomId:) int32_t roomId;
@property (nonatomic, strong, getter=userName, setter=setUserName:) NSString * userName;
@property (nonatomic, strong, getter=reason, setter=setReason:) NSString * reason;
@property (nonatomic, getter=ban, setter=setBan:) BOOL ban;
@property (nonatomic, getter=duration, setter=setDuration:) int32_t duration;
#endif

- (id) initWithZoneId: (int32_t) zoneId roomId: (int32_t) roomId userName: (NSString *) userName reason: (NSString *) reason ban: (BOOL) ban duration: (int32_t) duration;

- (void) read: (id <TProtocol>) inProtocol;
- (void) write: (id <TProtocol>) outProtocol;

- (int32_t) zoneId;
- (void) setZoneId: (int32_t) zoneId;
- (BOOL) zoneIdIsSet;

- (int32_t) roomId;
- (void) setRoomId: (int32_t) roomId;
- (BOOL) roomIdIsSet;

- (NSString *) userName;
- (void) setUserName: (NSString *) userName;
- (BOOL) userNameIsSet;

- (NSString *) reason;
- (void) setReason: (NSString *) reason;
- (BOOL) reasonIsSet;

- (BOOL) ban;
- (void) setBan: (BOOL) ban;
- (BOOL) banIsSet;

- (int32_t) duration;
- (void) setDuration: (int32_t) duration;
- (BOOL) durationIsSet;

@end

@interface ThriftEvictUserFromRoomRequestConstants : NSObject {
}
@end
