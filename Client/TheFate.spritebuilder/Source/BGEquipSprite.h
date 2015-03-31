//
//  BGEquipSprite.h
//  TheFate
//
//  Created by Killua Liu on 3/20/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import "BGCardSprite.h"

@interface BGEquipSprite : BGCardSprite {
    CCLabelBMFont *_avatarNameLabel;
}

@property (nonatomic, strong, readonly) CCLabelBMFont *avatarNameLabel;

@end
