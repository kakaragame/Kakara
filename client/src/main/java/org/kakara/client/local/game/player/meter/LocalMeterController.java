package org.kakara.client.local.game.player.meter;

import org.kakara.core.common.player.meter.GameMeters;
import org.kakara.core.common.player.meter.Meter;
import org.kakara.core.common.player.meter.PlayerMeter;
import org.kakara.core.common.player.meter.PlayerMeterController;
import org.kakara.core.server.player.meter.ServerPlayerMeter;

import java.util.HashMap;
import java.util.Map;

public class LocalMeterController implements PlayerMeterController {
    private Map<Meter, ServerPlayerMeter> meterMap = new HashMap<>();

    @Override
    public PlayerMeter getPlayerMeter(Meter meter) {
        return meterMap.get(meter);
    }

}
