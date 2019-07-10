/*
 * Copyright 2016 The International Internet Preservation Consortium.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.netpreserve.commons.uri.parser;

import java.math.BigInteger;

import org.junit.Test;
import org.netpreserve.commons.uri.UriBuilderConfig;
import org.netpreserve.commons.uri.UriException;

import static org.assertj.core.api.Assertions.*;

/**
 *
 */
public class IpUtilTest {

    /**
     * Test of checkAndNormalizeIpv6Base85 method, of class IpUtil.
     */
    @Test
    public void testSerializeIpv6Base85() {
        BigInteger ipv6Number = IpUtil.parseIpv6("1080:0:0:0:8:800:200C:417A");
        assertThat(IpUtil.serializeIpv6Base85(ipv6Number)).isEqualTo("4)+k&C#VzJ4br>0wv%Yp");
    }

    @Test
    public void testSerializeIpv6() {
        UriBuilderConfig.HexCase hexCase = UriBuilderConfig.HexCase.UPPER;

        BigInteger ipv6Number = IpUtil.parseIpv6("1080:0:0:0:8:800:200C:417A");
        assertThat(IpUtil.serializeIpv6(ipv6Number, hexCase)).isEqualTo("1080::8:800:200C:417A");

        ipv6Number = IpUtil.parseIpv6("FEDC:BA98:7654:3210:FEDC:BA98:7654:3210");
        assertThat(IpUtil.serializeIpv6(ipv6Number, hexCase)).isEqualTo("FEDC:BA98:7654:3210:FEDC:BA98:7654:3210");

        ipv6Number = IpUtil.parseIpv6("FEDC:BA98:7654:3210:FEDC:BA98:7654:0");
        assertThat(IpUtil.serializeIpv6(ipv6Number, hexCase)).isEqualTo("FEDC:BA98:7654:3210:FEDC:BA98:7654:0");

        ipv6Number = IpUtil.parseIpv6("FEDC:BA98:7654:3210:FEDC:BA98:0:0");
        assertThat(IpUtil.serializeIpv6(ipv6Number, hexCase)).isEqualTo("FEDC:BA98:7654:3210:FEDC:BA98::");

        ipv6Number = IpUtil.parseIpv6("0:BA98:7654:3210:FEDC:BA98:7654:0");
        assertThat(IpUtil.serializeIpv6(ipv6Number, hexCase)).isEqualTo("0:BA98:7654:3210:FEDC:BA98:7654:0");

        ipv6Number = IpUtil.parseIpv6("0:0:7654:3210:FEDC:BA98:0:0");
        assertThat(IpUtil.serializeIpv6(ipv6Number, hexCase)).isEqualTo("::7654:3210:FEDC:BA98:0:0");

        ipv6Number = IpUtil.parseIpv6("FEDC:0:7654:0:0:BA98:7654:3210");
        assertThat(IpUtil.serializeIpv6(ipv6Number, hexCase)).isEqualTo("FEDC:0:7654::BA98:7654:3210");

        assertThat(IpUtil.serializeIpv6(BigInteger.ZERO, hexCase)).isEqualTo("::");
    }

    /**
     * Test of checkAndNormalizeIpv6Base85 method, of class IpUtil.
     */
    @Test
    public void testParseIpv6() {
        assertThat(IpUtil.parseIpv6("0:0:0:0:0:0:0:0")).isEqualTo(BigInteger.ZERO);
        assertThatExceptionOfType(UriException.class).isThrownBy(() -> IpUtil.parseIpv6("0:0:0:0:0:0:0:0:0"));
        assertThatExceptionOfType(UriException.class).isThrownBy(() -> IpUtil.parseIpv6("0:0:0:0:0:0:0"));
        assertThat(IpUtil.parseIpv6("::")).isEqualTo(BigInteger.ZERO);

        BigInteger expected = new BigInteger("21932261930451111902915077091070067066");
        assertThat(IpUtil.parseIpv6("1080:0:0:0:8:800:200C:417A")).isEqualTo(expected);
        assertThat(IpUtil.parseIpv6("1080::8:800:200C:417A")).isEqualTo(expected);
        assertThat(IpUtil.parseIpv6("4)+k&C#VzJ4br>0wv%Yp")).isEqualTo(expected);

        // ::7f00:1
        expected = new BigInteger("2130706433");
        assertThat(IpUtil.parseIpv6("::7f00:1")).isEqualTo(expected);
        assertThat(IpUtil.parseIpv6("::127.0.0.1")).isEqualTo(expected);
        assertThat(IpUtil.parseIpv6("0:0:0:0:0:0:127.0.0.1")).isEqualTo(expected);

        // 2001::7f00:1
        expected = new BigInteger("42540488161975842760550356427430952961");
        assertThat(IpUtil.parseIpv6("2001:0:0:0:0:0:127.0.0.1")).isEqualTo(expected);
        assertThat(IpUtil.parseIpv6("2001::127.0.0.1")).isEqualTo(expected);

        // 0:2001::7f00:1
        expected = new BigInteger("649116335479367717903907715809281");
        assertThat(IpUtil.parseIpv6("0:2001:0:0:0:0:127.0.0.1")).isEqualTo(expected);
        assertThat(IpUtil.parseIpv6("0:2001::127.0.0.1")).isEqualTo(expected);
    }

    /**
     * Test of checkIpv4 method, of class IpUtil.
     */
    @Test
    public void testCheckIpv4() {
        assertThat(IpUtil.checkIpv4("192.168.0.1")).isTrue();
        assertThatExceptionOfType(UriException.class).isThrownBy(() -> IpUtil.checkIpv4("192.168.0.257"));
        assertThat(IpUtil.checkIpv4("0Xc0.0250.01")).isFalse();
    }

    /**
     * Test of checkAndNormalizeIpv4 method, of class IpUtil.
     */
    @Test
    public void testCheckAndNormalizeIpv4() {
        assertThat(IpUtil.checkAndNormalizeIpv4("192.168.0.1")).isEqualTo("192.168.0.1");
        assertThat(IpUtil.checkAndNormalizeIpv4("192.168.0.1.")).isEqualTo("192.168.0.1");
        assertThat(IpUtil.checkAndNormalizeIpv4("0xc0.0250.01")).isEqualTo("192.168.0.1");
        assertThat(IpUtil.checkAndNormalizeIpv4("0Xc0.0250.01")).isEqualTo("192.168.0.1");
        assertThat(IpUtil.checkAndNormalizeIpv4("%30%78%63%30%2e%30%32%35%30.01")).isEqualTo("192.168.0.1");
        assertThat(IpUtil.checkAndNormalizeIpv4("%30%78%63%30%2e%30%32%35%30.01%2e")).isEqualTo("192.168.0.1");
        assertThatExceptionOfType(UriException.class).isThrownBy(() -> IpUtil.checkAndNormalizeIpv4("192.168.0.257"));
        assertThat(IpUtil.checkAndNormalizeIpv4("not an ip 192.168.0.1")).isNull();
        assertThat(IpUtil.checkAndNormalizeIpv4("8584905.fls.doubleclick.net")).isNull();
        assertThat(IpUtil.checkAndNormalizeIpv4("8584905.123.doubleclick.net")).isNull();
        assertThat(IpUtil.checkAndNormalizeIpv4("8584905.654.doubleclick.eet")).isNull();
        assertThatExceptionOfType(UriException.class).isThrownBy(() -> IpUtil.checkAndNormalizeIpv4("8584905.168.0.254"));
    }
}
