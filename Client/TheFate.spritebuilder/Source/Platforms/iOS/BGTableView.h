//
//  BGTableView.h
//  TheFate
//
//  Created by Killua Liu on 4/3/14.
//  Copyright (c) 2014 Apportable. All rights reserved.
//

#if __CC_PLATFORM_IOS
#import <UIKit/UIKit.h>
#import "BGTableViewCell.h"

@interface BGTableView : UITableView <UIScrollViewDelegate>
#else
@interface BGTableView
#endif

@end
