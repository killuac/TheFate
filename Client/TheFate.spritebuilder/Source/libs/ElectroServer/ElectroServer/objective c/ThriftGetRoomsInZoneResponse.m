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

#import "ThriftRoomListEntry.h"

#import "ThriftGetRoomsInZoneResponse.h"


@implementation ThriftGetRoomsInZoneResponseConstants
+ (void) initialize {
}
@end

@implementation ThriftGetRoomsInZoneResponse

- (id) initWithZoneId: (int32_t) zoneId zoneName: (NSString *) zoneName entries: (NSArray *) entries
{
  self = [super init];
  __zoneId = zoneId;
  __zoneId_isset = YES;
  __zoneName = zoneName;
  __zoneName_isset = YES;
  __entries = entries;
  __entries_isset = YES;
  return self;
}

- (id) initWithCoder: (NSCoder *) decoder
{
  self = [super init];
  if ([decoder containsValueForKey: @"zoneId"])
  {
    __zoneId = [decoder decodeInt32ForKey: @"zoneId"];
    __zoneId_isset = YES;
  }
  if ([decoder containsValueForKey: @"zoneName"])
  {
    __zoneName = [decoder decodeObjectForKey: @"zoneName"];
    __zoneName_isset = YES;
  }
  if ([decoder containsValueForKey: @"entries"])
  {
    __entries = [decoder decodeObjectForKey: @"entries"];
    __entries_isset = YES;
  }
  return self;
}

- (void) encodeWithCoder: (NSCoder *) encoder
{
  if (__zoneId_isset)
  {
    [encoder encodeInt32: __zoneId forKey: @"zoneId"];
  }
  if (__zoneName_isset)
  {
    [encoder encodeObject: __zoneName forKey: @"zoneName"];
  }
  if (__entries_isset)
  {
    [encoder encodeObject: __entries forKey: @"entries"];
  }
}


- (int32_t) zoneId {
  return __zoneId;
}

- (void) setZoneId: (int32_t) zoneId {
  __zoneId = zoneId;
  __zoneId_isset = YES;
}

- (BOOL) zoneIdIsSet {
  return __zoneId_isset;
}

- (void) unsetZoneId {
  __zoneId_isset = NO;
}

- (NSString *) zoneName {
  return __zoneName;
}

- (void) setZoneName: (NSString *) zoneName {
  __zoneName = zoneName;
  __zoneName_isset = YES;
}

- (BOOL) zoneNameIsSet {
  return __zoneName_isset;
}

- (void) unsetZoneName {
  __zoneName = nil;
  __zoneName_isset = NO;
}

- (NSArray *) entries {
  return __entries;
}

- (void) setEntries: (NSArray *) entries {
  __entries = entries;
  __entries_isset = YES;
}

- (BOOL) entriesIsSet {
  return __entries_isset;
}

- (void) unsetEntries {
  __entries = nil;
  __entries_isset = NO;
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
          [self setZoneId: fieldValue];
        } else { 
          [TProtocolUtil skipType: fieldType onProtocol: inProtocol];
        }
        break;
      case 2:
        if (fieldType == TType_STRING) {
          NSString * fieldValue = [inProtocol readString];
          [self setZoneName: fieldValue];
        } else { 
          [TProtocolUtil skipType: fieldType onProtocol: inProtocol];
        }
        break;
      case 3:
        if (fieldType == TType_LIST) {
          int _size0;
          [inProtocol readListBeginReturningElementType: NULL size: &_size0];
          NSMutableArray * fieldValue = [[NSMutableArray alloc] initWithCapacity: _size0];
          int _i1;
          for (_i1 = 0; _i1 < _size0; ++_i1)
          {
            ThriftRoomListEntry *_elem2 = [[ThriftRoomListEntry alloc] init];
            [_elem2 read: inProtocol];
            [fieldValue addObject: _elem2];
          }
          [inProtocol readListEnd];
          [self setEntries: fieldValue];
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
  [outProtocol writeStructBeginWithName: @"ThriftGetRoomsInZoneResponse"];
  if (__zoneId_isset) {
    [outProtocol writeFieldBeginWithName: @"zoneId" type: TType_I32 fieldID: 1];
    [outProtocol writeI32: __zoneId];
    [outProtocol writeFieldEnd];
  }
  if (__zoneName_isset) {
    if (__zoneName != nil) {
      [outProtocol writeFieldBeginWithName: @"zoneName" type: TType_STRING fieldID: 2];
      [outProtocol writeString: __zoneName];
      [outProtocol writeFieldEnd];
    }
  }
  if (__entries_isset) {
    if (__entries != nil) {
      [outProtocol writeFieldBeginWithName: @"entries" type: TType_LIST fieldID: 3];
      {
        [outProtocol writeListBeginWithElementType: TType_STRUCT size: [__entries count]];
        for (NSUInteger i4 = 0; i4 < [__entries count]; i4++)
        {
          [[__entries objectAtIndex: i4] write: outProtocol];
        }
        [outProtocol writeListEnd];
      }
      [outProtocol writeFieldEnd];
    }
  }
  [outProtocol writeFieldStop];
  [outProtocol writeStructEnd];
}

- (NSString *) description {
  NSMutableString * ms = [NSMutableString stringWithString: @"ThriftGetRoomsInZoneResponse("];
  [ms appendString: @"zoneId:"];
  [ms appendFormat: @"%i", __zoneId];
  [ms appendString: @",zoneName:"];
  [ms appendFormat: @"\"%@\"", __zoneName];
  [ms appendString: @",entries:"];
  [ms appendFormat: @"%@", __entries];
  [ms appendString: @")"];
  return [NSString stringWithString: ms];
}

@end

