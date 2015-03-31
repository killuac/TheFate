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


#import "ThriftRegisterUDPConnectionRequest.h"


@implementation ThriftRegisterUDPConnectionRequestConstants
+ (void) initialize {
}
@end

@implementation ThriftRegisterUDPConnectionRequest

- (id) initWithPort: (int32_t) port
{
  self = [super init];
  __port = port;
  __port_isset = YES;
  return self;
}

- (id) initWithCoder: (NSCoder *) decoder
{
  self = [super init];
  if ([decoder containsValueForKey: @"port"])
  {
    __port = [decoder decodeInt32ForKey: @"port"];
    __port_isset = YES;
  }
  return self;
}

- (void) encodeWithCoder: (NSCoder *) encoder
{
  if (__port_isset)
  {
    [encoder encodeInt32: __port forKey: @"port"];
  }
}


- (int32_t) port {
  return __port;
}

- (void) setPort: (int32_t) port {
  __port = port;
  __port_isset = YES;
}

- (BOOL) portIsSet {
  return __port_isset;
}

- (void) unsetPort {
  __port_isset = NO;
}

- (void) read: (id <TProtocol>) inProtocol
{
  NSString * fieldName;
  int fieldType;
  int fieldID;

  [inProtocol readStructBeginReturningName: NULL];
  while (true)
  {
    [inProtocol readFieldBeginReturningName: &fieldName type: &fieldType fieldID: &fieldID];
    if (fieldType == TType_STOP) { 
      break;
    }
    switch (fieldID)
    {
      case 1:
        if (fieldType == TType_I32) {
          int32_t fieldValue = [inProtocol readI32];
          [self setPort: fieldValue];
        } else { 
          [TProtocolUtil skipType: fieldType onProtocol: inProtocol];
        }
        break;
      default:
        [TProtocolUtil skipType: fieldType onProtocol: inProtocol];
        break;
    }
    [inProtocol readFieldEnd];
  }
  [inProtocol readStructEnd];
}

- (void) write: (id <TProtocol>) outProtocol {
  [outProtocol writeStructBeginWithName: @"ThriftRegisterUDPConnectionRequest"];
  if (__port_isset) {
    [outProtocol writeFieldBeginWithName: @"port" type: TType_I32 fieldID: 1];
    [outProtocol writeI32: __port];
    [outProtocol writeFieldEnd];
  }
  [outProtocol writeFieldStop];
  [outProtocol writeStructEnd];
}

- (NSString *) description {
  NSMutableString * ms = [NSMutableString stringWithString: @"ThriftRegisterUDPConnectionRequest("];
  [ms appendString: @"port:"];
  [ms appendFormat: @"%i", __port];
  [ms appendString: @")"];
  return [NSString stringWithString: ms];
}

@end

